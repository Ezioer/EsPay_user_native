package com.easou.androidsdk.login.service;

public class MoneyBaseInfo {
    private String playerId;
    private String playerName;
    private String playerLevel;
    private String serverId;
    private String serverName;
    private int moneyBalance;
    private Long gainTimeRemaining;
    private Long drawTimeRemaining;
    private String activityRule;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(String playerLevel) {
        this.playerLevel = playerLevel;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(int moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public Long getGainTimeRemaining() {
        return gainTimeRemaining;
    }

    public void setGainTimeRemaining(Long gainTimeRemaining) {
        this.gainTimeRemaining = gainTimeRemaining;
    }

    public Long getDrawTimeRemaining() {
        return drawTimeRemaining;
    }

    public void setDrawTimeRemaining(Long drawTimeRemaining) {
        this.drawTimeRemaining = drawTimeRemaining;
    }

    public String getActivityRule() {
        return activityRule;
    }

    public void setActivityRule(String activityRule) {
        this.activityRule = activityRule;
    }
}
