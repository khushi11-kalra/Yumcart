import { Button, Card } from "@mui/material";
import HomeIcon from "@mui/icons-material/Home";
import React from "react";

const AddressCard = ({ handleSelectAddress = () => {}, item, showButton }) => {
  return (
    <Card className="flex space-x-5 w-64 p-5">
      <HomeIcon />

      <div className="space-y-3 text-gray-500">
        <h1 className="font-semibold text-lg text-white">Home</h1>
        <p>
          {item?.streetAddress}, {item?.postalCode}, {item?.state}, {item?.country}
        </p>

        {showButton && (
          <Button
            onClick={() => handleSelectAddress(item)}
            variant="outlined"
            sx={{ width: "100%" }} // Fix for Material UI styling
          >
            Select
          </Button>
        )}
      </div>
    </Card>
  );
};

export default AddressCard;
