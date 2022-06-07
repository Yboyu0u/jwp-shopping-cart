package woowacourse.shoppingcart.ui.dto.request;

import java.util.List;

public class CartDeleteRequest {

    private List<Long> cartIds;

    public CartDeleteRequest() {
    }

    public CartDeleteRequest(List<Long> cartIds) {
        this.cartIds = cartIds;
    }

    public List<Long> getCartIds() {
        return cartIds;
    }
}
