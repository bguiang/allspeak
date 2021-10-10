import React from "react";
import useStyles from "../styles";
import {
  Container,
  TextField,
  Button,
  Typography,
  InputLabel,
  Select,
  MenuItem,
  FormControl,
  Card,
  Paper,
  CardContent,
} from "@material-ui/core";

const Navbar = () => {
  let classes = useStyles();
  return (
    <Container component="main" maxWidth="md" classes={classes.mainContainer}>
      <div className={classes.navbar}>
        <h1 className={classes.navbarTitle}>AllSpeak</h1>
      </div>
    </Container>
  );
};

export default Navbar;
