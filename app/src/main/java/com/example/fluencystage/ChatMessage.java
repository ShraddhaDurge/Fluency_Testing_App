package com.example.fluencystage;

class ChatMessage {
    private String content;
    private boolean isWord;

    public ChatMessage(String content, boolean isWord) {
        this.content = content;
        this.isWord = isWord;
    }

    public String getContent() {
        return content;
    }

    public boolean isWord() {
        return isWord;
    }
}

