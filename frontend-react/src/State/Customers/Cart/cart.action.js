import { api } from "../../../config/api";
import {
  findCartFailure,
  findCartRequest,
  findCartSuccess,
  getAllCartItemsFailure,
  getAllCartItemsRequest,
  getAllCartItemsSuccess,
} from "./ActionCreators";

import { 
  ADD_ITEM_TO_CART_FAILURE, 
  ADD_ITEM_TO_CART_REQUEST, 
  ADD_ITEM_TO_CART_SUCCESS, 
  CLEARE_CART_FAILURE, 
  CLEARE_CART_REQUEST, 
  CLEARE_CART_SUCCESS, 
  REMOVE_CARTITEM_FAILURE, 
  REMOVE_CARTITEM_REQUEST, 
  REMOVE_CARTITEM_SUCCESS, 
  UPDATE_CARTITEM_FAILURE, 
  UPDATE_CARTITEM_REQUEST, 
  UPDATE_CARTITEM_SUCCESS 
} from "./ActionTypes";

// ✅ Fix: Ensure duplicate cart items are not added
export const addItemToCart = (reqData) => {
  return async (dispatch, getState) => {
    dispatch({ type: ADD_ITEM_TO_CART_REQUEST });

    try {
      const state = getState();
      const existingItem = state.cart.cartItems.find(
        (item) => item.food.id === reqData.cartItem.foodId
      );

      let data;
      if (existingItem) {
        // If item exists, update quantity instead of adding duplicate
        const updateReqData = {
          cartItemId: existingItem.id,
          quantity: existingItem.quantity + reqData.cartItem.quantity
        };
        const response = await api.put(`/api/cart-item/update`, updateReqData, {
          headers: {
            Authorization: `Bearer ${reqData.token}`,
          },
        });
        data = response.data;
      } else {
        // If item does not exist, add it to the cart
        const response = await api.put(`/api/cart/add`, reqData.cartItem, {
          headers: {
            Authorization: `Bearer ${reqData.token}`,
          },
        });
        data = response.data;
      }

      console.log("Cart updated successfully:", data);
      dispatch({ type: ADD_ITEM_TO_CART_SUCCESS, payload: data });

    } catch (error) {
      console.log("Error in adding item to cart:", error);
      dispatch({ type: ADD_ITEM_TO_CART_FAILURE, payload: error.message });
    }
  };
};

// ✅ No changes needed here
export const findCart = (token) => {
  return async (dispatch) => {
    dispatch(findCartRequest());
    try {
      const response = await api.get(`/api/cart/`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      dispatch(findCartSuccess(response.data));
    } catch (error) {
      dispatch(findCartFailure(error));
    }
  };
};

export const getAllCartItems = (reqData) => {
  return async (dispatch) => {
    dispatch(getAllCartItemsRequest());
    try {
      const response = await api.get(`/api/carts/${reqData.cartId}/items`, {
        headers: {
          Authorization: `Bearer ${reqData.token}`,
        },
      });
      dispatch(getAllCartItemsSuccess(response.data));
    } catch (error) {
      dispatch(getAllCartItemsFailure(error));
    }
  };
};

export const updateCartItem = (reqData) => {
  return async (dispatch) => {
    dispatch({ type: UPDATE_CARTITEM_REQUEST });
    try {
      const { data } = await api.put(`/api/cart-item/update`, reqData.data, {
        headers: {
          Authorization: `Bearer ${reqData.jwt}`,
        },
      });
      console.log("Updated cart item:", data);
      dispatch({ type: UPDATE_CARTITEM_SUCCESS, payload: data });
    } catch (error) {
      console.log("Error in updating cart item:", error);
      dispatch({ type: UPDATE_CARTITEM_FAILURE, payload: error.message });
    }
  };
};

export const removeCartItem = ({ cartItemId, jwt }) => {
  return async (dispatch) => {
    dispatch({ type: REMOVE_CARTITEM_REQUEST });
    try {
      const { data } = await api.delete(`/api/cart-item/${cartItemId}/remove`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });
      console.log("Removed cart item:", data);
      dispatch({ type: REMOVE_CARTITEM_SUCCESS, payload: cartItemId });
    } catch (error) {
      console.log("Error in removing cart item:", error);
      dispatch({ type: REMOVE_CARTITEM_FAILURE, payload: error.message });
    }
  };
};

export const clearCartAction = () => {
  return async (dispatch) => {
    dispatch({ type: CLEARE_CART_REQUEST });
    try {
      const { data } = await api.put(`/api/cart/clear`, {}, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwt")}`,
        },
      });

      dispatch({ type: CLEARE_CART_SUCCESS, payload: data });
      console.log("Cleared cart successfully:", data);
    } catch (error) {
      console.log("Error in clearing cart:", error);
      dispatch({ type: CLEARE_CART_FAILURE, payload: error.message });
    }
  };
};
