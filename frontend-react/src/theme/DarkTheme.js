import { createTheme } from "@mui/material";

const darkTheme = createTheme({
  palette: {
    mode: "dark", // Dark theme mode
    primary: {
      main: "#1976D2", // Blue primary color (Navbar & Buttons)
    },
    secondary: {
      main: "#FF9800", // Orange secondary color (Highlight Elements)
    },
    black: {
      main: "#1E293B", // Dark blue-gray for a modern look
    },
    background: {
      main: "#0A1929", // Deep navy background
      default: "#102A43", // Slightly lighter background for better contrast
      paper: "#0A1929", // Background color for cards and modals
    },
    textColor: {
      main: "#E0E0E0", // Light gray text for better readability
    },
  },
});

export default darkTheme;
