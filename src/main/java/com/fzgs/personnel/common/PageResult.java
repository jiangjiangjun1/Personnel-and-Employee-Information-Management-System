package com.fzgs.personnel.common;

import java.util.List;

public record PageResult<T>(
        List<T> items,
        int page,
        int size,
        long total,
        int totalPages
) {
    public List<T> getItems() { return items; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotal() { return total; }
    public int getTotalPages() { return totalPages; }
}
