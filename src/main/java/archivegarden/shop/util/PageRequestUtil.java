package archivegarden.shop.util;

import org.springframework.data.domain.PageRequest;

public class PageRequestUtil {

    private static final int DEFAULT_PAGE_SIZE = 10;

    public static PageRequest of(int page) {
        return PageRequest.of(Math.max(page - 1, 0), DEFAULT_PAGE_SIZE);
    }

    public static PageRequest of(int page, int size) {
        return PageRequest.of(Math.max(page - 1, 0), size);
    }
}

