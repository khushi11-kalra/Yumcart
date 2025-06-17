import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Button,
  Checkbox,
  FormControlLabel,
  FormGroup,
} from "@mui/material";
import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { addItemToCart } from "../../../State/Customers/Cart/cart.action";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

const MenuItemCard = ({ item }) => {
  const dispatch = useDispatch();
  const [selectedIngredients, setSelectedIngredients] = useState([]);

  const handleCheckboxChange = (itemName) => {
    setSelectedIngredients((prevSelected) =>
      prevSelected.includes(itemName)
        ? prevSelected.filter((name) => name !== itemName)
        : [...prevSelected, itemName]
    );
  };

  const handleAddItemToCart = (e) => {
    e.preventDefault(); // Prevents page refresh on form submission

    const data = {
      token: localStorage.getItem("jwt"),
      cartItem: {
        menuItemId: item.id,
        quantity: 1,
        ingredients: selectedIngredients,
      },
    };
    dispatch(addItemToCart(data));
  };

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <div className="lg:flex items-center justify-between">
          <div className="lg:flex items-center lg:space-x-5">
            <img
              className="w-[7rem] h-[7rem] object-cover"
              src={item.images?.[0] || "default-image-url.jpg"} // Fallback image
              alt={item.name || "Food item"}
            />
            <div className="space-y-1 lg:space-y-5 lg:max-w-2xl">
              <p className="font-semibold text-xl">{item.name}</p>
              <p>₹{item.price}</p>
              <p className="text-gray-400">{item.description}</p>
            </div>
          </div>
        </div>
      </AccordionSummary>

      <AccordionDetails>
        <form onSubmit={handleAddItemToCart}>
          {/* Display ingredients if available */}
          {item.ingredients?.length > 0 && (
            <div className="flex gap-5 flex-wrap">
              <div className="pr-5">
                <p className="font-semibold">Ingredients</p>
                <FormGroup>
                  {item.ingredients.map((ingredient) => (
                    <FormControlLabel
                      key={ingredient.name}
                      control={
                        <Checkbox
                          checked={selectedIngredients.includes(ingredient.name)}
                          onChange={() => handleCheckboxChange(ingredient.name)}
                          disabled={!ingredient.inStock}
                        />
                      }
                      label={ingredient.name}
                    />
                  ))}
                </FormGroup>
              </div>
            </div>
          )}

          {/* Add to Cart Button */}
          <div className="pt-5">
            <Button variant="contained" disabled={!item.available} type="submit">
              {item.available ? "Add To Cart" : "Out of Stock"}
            </Button>
          </div>
        </form>
      </AccordionDetails>
    </Accordion>
  );
};

export default MenuItemCard;
