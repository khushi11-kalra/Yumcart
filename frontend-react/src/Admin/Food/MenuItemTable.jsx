import {
  Avatar,
  Backdrop,
  Box,
  Button,
  Card,
  CardHeader,
  CircularProgress,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import React, { useEffect } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  deleteFoodAction,
  getMenuItemsByRestaurantId,
  updateMenuItemsAvailability,
} from "../../State/Customers/Menu/menu.action";
import HorizontalRuleIcon from "@mui/icons-material/HorizontalRule";
import DeleteIcon from "@mui/icons-material/Delete";
import { Create } from "@mui/icons-material";

const MenuItemTable = ({ isDashboard, name }) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { menu, restaurant, auth } = useSelector((store) => store);
  const jwt = localStorage.getItem("jwt") || auth.jwt;

  useEffect(() => {
    if (restaurant.usersRestaurant) {
      dispatch(
        getMenuItemsByRestaurantId({
          restaurantId: restaurant.usersRestaurant.id,
          jwt,
          seasonal: false,
          vegetarian: false,
          nonveg: false,
          foodCategory: "",
        })
      );
    }
  }, [dispatch, restaurant.usersRestaurant, jwt]);

  const handleFoodAvailability = (foodId) => {
    dispatch(updateMenuItemsAvailability({ foodId, jwt }));
  };

  const handleDeleteFood = (foodId) => {
    dispatch(deleteFoodAction({ foodId, jwt }));
  };

  return (
    <Box width={"100%"}>
      <Card className="mt-1">
        <CardHeader
          title={name}
          sx={{
            pt: 2,
            alignItems: "center",
            "& .MuiCardHeader-action": { mt: 0.6 },
          }}
          action={
            <IconButton onClick={() => navigate("/admin/restaurant/add-menu")}>
              <Create />
            </IconButton>
          }
        />
        <TableContainer>
          <Table aria-label="table in dashboard">
            <TableHead>
              <TableRow>
                <TableCell>Image</TableCell>
                <TableCell>Title</TableCell>
                {!isDashboard && <TableCell>Ingredients</TableCell>}
                <TableCell sx={{ textAlign: "center" }}>Price</TableCell>
                <TableCell sx={{ textAlign: "center" }}>Availability</TableCell>
                {!isDashboard && (
                  <TableCell sx={{ textAlign: "center" }}>Delete</TableCell>
                )}
              </TableRow>
            </TableHead>
            <TableBody>
              {menu.menuItems?.map((item) => (
                <TableRow
                  hover
                  key={item.id}
                  sx={{ "&:last-of-type td, &:last-of-type th": { border: 0 } }}
                >
                  <TableCell>
                    <Avatar
                      alt={item.name}
                      src={item.images?.[0] || "default-image-url.jpg"}
                    />
                  </TableCell>

                  <TableCell>
                    <Typography sx={{ fontWeight: 500, fontSize: "0.875rem" }}>
                      {item.name}
                    </Typography>
                    <Typography variant="caption">{item.brand}</Typography>
                  </TableCell>

                  {!isDashboard && (
                    <TableCell>
                      {item.ingredients?.length > 0 ? (
                        item.ingredients.map((ingredient) => (
                          <div
                            key={ingredient.id}
                            className="flex gap-1 items-center"
                          >
                            <HorizontalRuleIcon sx={{ fontSize: "1rem" }} />
                            <p>{ingredient.name}</p>
                          </div>
                        ))
                      ) : (
                        <Typography variant="caption" color="textSecondary">
                          No ingredients
                        </Typography>
                      )}
                    </TableCell>
                  )}

                  <TableCell sx={{ textAlign: "center" }}>
                    ₹{item.price}
                  </TableCell>

                  <TableCell sx={{ textAlign: "center" }}>
                    <Button
                      color={item.available ? "success" : "error"}
                      variant="text"
                      onClick={() => handleFoodAvailability(item.id)}
                    >
                      {item.available ? "In Stock" : "Out of Stock"}
                    </Button>
                  </TableCell>

                  {!isDashboard && (
                    <TableCell sx={{ textAlign: "center" }}>
                      <IconButton onClick={() => handleDeleteFood(item.id)}>
                        <DeleteIcon color="error" />
                      </IconButton>
                    </TableCell>
                  )}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>

      <Backdrop
        sx={{ color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1 }}
        open={menu.loading}
      >
        <CircularProgress color="inherit" />
      </Backdrop>
    </Box>
  );
};

export default MenuItemTable;
