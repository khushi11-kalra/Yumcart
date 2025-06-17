import React from "react";
import AddressCard from "../../components/Address/AddressCard";
import { useSelector } from "react-redux";

const UsersAddresses = () => {
  const { auth } = useSelector((state) => state);

  // Remove duplicates before rendering
  const uniqueAddresses = Array.from(new Set(auth.user?.addresses?.map(JSON.stringify))).map(JSON.parse);

  return (
    <div>
      <div className="flex items-center flex-col lg:px-10">
        <h1 className="text-xl text-center py-7 font-semibold">Addresses</h1>
        <div className="flex justify-center flex-wrap gap-3">
          {uniqueAddresses.length > 0 ? (
            uniqueAddresses.map((item) => (
              <AddressCard key={item.id || item.postalCode} item={item} />
            ))
          ) : (
            <p className="text-gray-500">No saved addresses</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default UsersAddresses;
