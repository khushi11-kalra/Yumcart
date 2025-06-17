import { Create } from "@mui/icons-material";
import {
  Box,
  Button,
  Card,
  CardHeader,
  Grid,
  IconButton,
  Modal,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";

import CreateIngredientForm from "./CreateIngredientForm";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  getIngredientsOfRestaurant,
  updateStockOfIngredient,
} from "../../State/Admin/Ingredients/Action";

const style = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 400,
  bgcolor: "background.paper",
  boxShadow: 24,
  outline: "none",
  p: 4,
};

const Ingredients = () => {
  const dispatch = useDispatch();
  const { auth, restaurant, ingredients } = useSelector((store) => store);
  const jwt = localStorage.getItem("jwt");

  const [openIngredient, setOpenIngredient] = useState(false);
  const handleOpenIngredient = () => setOpenIngredient(true);
  const handleCloseIngredient = () => setOpenIngredient(false);

  const handleUpdateStocke = (id) => {
    dispatch(updateStockOfIngredient({ id, jwt }));
  };

  return (
    <div className="px-2">
      <Grid container spacing={1}>
        <Grid item xs={12}>
          <Card className="mt-1">
            <CardHeader
              title={"Ingredients"}
              sx={{
                pt: 2,
                alignItems: "center",
                "& .MuiCardHeader-action": { mt: 0.6 },
              }}
              action={
                <IconButton onClick={handleOpenIngredient}>
                  <Create />
                </IconButton>
              }
            />
            <TableContainer className="h-[88vh] overflow-y-scroll">
              <Table aria-label="table in dashboard">
                <TableHead>
                  <TableRow>
                    <TableCell>Id</TableCell>
                    <TableCell>Name</TableCell>
                    <TableCell>Availability</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {ingredients.ingredients.map((item) => (
                    <TableRow
                      className="cursor-pointer"
                      hover
                      key={item.id}
                      sx={{
                        "&:last-of-type td, &:last-of-type th": { border: 0 },
                      }}
                    >
                      <TableCell>{item?.id}</TableCell>
                      <TableCell>{item.name}</TableCell>
                      <TableCell>
                        <Button
                          onClick={() => handleUpdateStocke(item.id)}
                          color={item.inStoke ? "success" : "primary"}
                        >
                          {item.inStoke ? "In Stock" : "Out of Stock"}
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Card>
        </Grid>
      </Grid>

      <Modal
        open={openIngredient}
        onClose={handleCloseIngredient}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <CreateIngredientForm handleClose={handleCloseIngredient} />
        </Box>
      </Modal>
    </div>
  );
};

export default Ingredients;
