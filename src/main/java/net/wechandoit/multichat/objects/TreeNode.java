package net.wechandoit.multichat.objects;

import java.util.HashMap;

public class TreeNode {
    private HashMap<Character, TreeNode> node;
    private boolean isEnd;

    public TreeNode() {
        isEnd = false;
        node = new HashMap<Character, TreeNode>();
    }

    public TreeNode(Character letter) {
        this();
    }


    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public void addChild(Character letter) {
        TreeNode childNode = new TreeNode(letter);
        node.put(letter, childNode);
    }

    public TreeNode getChildByLetter(Character letter) {
        return node.get(letter);
    }

    public boolean containsChild(Character letter) {
        return node.containsKey(letter);
    }
}
