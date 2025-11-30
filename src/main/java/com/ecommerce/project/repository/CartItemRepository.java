package com.ecommerce.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	@Query("SELECT ci FROM CartItem ci WHERE ci.cart.id =?1 AND ci.product.id =?2 ")
	CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

	@Query("SELECT c FROM Cart c where c.user.email = ?1 AND c.id = ?2")
	Cart findCartByEmailAndCartId(String emailId, Long cartId);

	
	 @Modifying	@Query("DELETE FROM CartItem ci WHERE ci.cart.id= ?1 AND ci.product.id=?2")
	void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);

}
