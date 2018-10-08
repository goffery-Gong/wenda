package com.nowcoder.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @ClassName: SensitiveService
 * @Description: 敏感词过滤，通过使用树来构建一个字典；然后将输入的串进行与树中的词进行比较
 *
 * @author Goffery Gong
 * @date 2018年9月28日 下午4:35:54
 */
@Service
public class SensitiveService implements InitializingBean {
	private static final Logger logger = LoggerFactory
			.getLogger(SensitiveService.class);
	private TrieNode rootNode = new TrieNode(); // 根节点

	/**
	 * 过滤
	 */
	public String filter(String text) {
		if (StringUtils.isBlank(text))
			return text;

		String replance = "***";
		TrieNode tempNode = rootNode;
		int begin = 0;	// begin总是不断向前，position匹配失败的时候，需要回滚。开始下标为0.
		int position = 0;	//当前比较的位置，下标从0开始
		StringBuilder result = new StringBuilder();

		while (position < text.length()) {
			char c = text.charAt(position);
			// 先判断字符：将不是英文字母和中文的字符过滤掉
			if (isSymbol(c)) {
				//如果在敏感词中之外的位置（即tempNode==根节点）加了空格之类，直接将字符输出到结果中，然后++begin,++positon;
				if (tempNode == rootNode) {
					result.append(c);
					++begin;
				}
				//如果在敏感词中间加了空格之类：“色 情”，position就直接跳过空格到下一个
				++position;
				continue;
			}

			tempNode = tempNode.getSubNode(c);

			if (tempNode == null) {
				// 没有以c开头的敏感词
				result.append(text.charAt(begin));
				position = begin + 1;//不是postion+1：因为可能出现“色哈情”这种，position移动到了哈上，但最终匹配失败的情况。
				begin = position;
				tempNode = rootNode;
			} else if (tempNode.isKeywordEnd()) {
				// 发现敏感词
				result.append(replance);
				position = position + 1;
				begin = position;
				tempNode = rootNode;
			} else {
				++position;
			}
		}
		result.append(text.substring(begin));
		return result.toString();
	}

	// 判断字符是否为其他字符
	private boolean isSymbol(char c) {
		int ic = (int) c;
		// 判断是否是英文字母或者东亚文 0x2E80-0x9FFF 东亚文字范围
		return !CharUtils.isAsciiAlphanumeric(c)
				&& (ic < 0x2E80 || ic > 0x9FFF);
	}

	/**
	 * 构造字典树
	 */
	@Override
	// 类初始化时读取敏感词文件
	public void afterPropertiesSet() throws Exception {
		try {
			InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("SensitiveWords.txt");
			InputStreamReader read = new InputStreamReader(in);
			BufferedReader reader = new BufferedReader(read);
			String lineTxt;
			while ((lineTxt = reader.readLine()) != null)
				addWord(lineTxt.trim());
			read.close();
		} catch (Exception e) {
			logger.error("读取敏感词文件出错" + e.getMessage());
		}
	}

	// 建立字典树,从根节点增加一条敏感词（调用一次只能增加一条敏感词）
	private void addWord(String lineTxt) {
		TrieNode tempNode = rootNode;

		// 遍历line，如果没有关键词就增加，如果有则在最后一个节点设置标志。
		for (int i = 0; i < lineTxt.length(); ++i) {
			Character c = lineTxt.charAt(i);
			if (isSymbol(c))
				continue;
			TrieNode subnode = tempNode.getSubNode(c);

			if (subnode == null) {
				tempNode.addSubNode(c, new TrieNode());
			}
			// 使得temp指向刚建立好的子节点
			tempNode = tempNode.getSubNode(c);

			if (i == lineTxt.length() - 1)
				tempNode.setKeywordEnd(true);
		}
	}

	// 前缀树类
	private class TrieNode {
		// 敏感词结尾标志
		private boolean end = false;

		private Map<Character, TrieNode> subNodes = new HashMap<>();

		// 向指定位置添加节点树
		void addSubNode(Character c, TrieNode node) {
			subNodes.put(c, node);
		}

		TrieNode getSubNode(Character key) {
			return subNodes.get(key);
		}

		boolean isKeywordEnd() {
			return end;
		}

		void setKeywordEnd(boolean end) {
			this.end = end;
		}
	}

	// 测试
	public static void main(String[] args) {
		SensitiveService s = new SensitiveService();
		s.addWord("色情");
		s.addWord("赌博");
		System.out.println(s.filter(" 你 好色情，爱赌 博"));
		System.out.println(s.isSymbol('宫'));
	}

}
