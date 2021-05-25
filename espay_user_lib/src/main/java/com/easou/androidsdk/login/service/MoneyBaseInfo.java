package com.easou.androidsdk.login.service;

public class MoneyBaseInfo {
    private String playerId;
    private String playerName;
    private String lvNcikName;
    private String serverId;
    private String serverName;
    private int totalMoney;
    private Long gainTimeRemaining;

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

    public Long getGainTimeRemaining() {
        return gainTimeRemaining;
    }

    public void setGainTimeRemaining(Long gainTimeRemaining) {
        this.gainTimeRemaining = gainTimeRemaining;
    }

    public String getLvNcikName() {
        return lvNcikName;
    }

    public void setLvNcikName(String lvNcikName) {
        this.lvNcikName = lvNcikName;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }
}
