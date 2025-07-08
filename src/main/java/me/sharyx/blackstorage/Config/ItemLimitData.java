package me.sharyx.blackstorage.Config;

public class ItemLimitData {
    private final int limit;
    private final int slot;
    private final int page;
    private final String name;
    private final String configName;

    public ItemLimitData(int limit, int slot, int page, String name, String configName) {
        this.limit = limit;
        this.slot = slot;
        this.page = page;
        this.name = name;
        this.configName = configName;
    }

    public int getLimit() { return limit; }
    public int getSlot() { return slot; }
    public int getPage() { return page; }
    public String getName() { return name; }
    public String getConfigName() { return configName; }
}

