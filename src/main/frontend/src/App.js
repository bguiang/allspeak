import logo from "./logo.svg";
import "./App.css";
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import { createTheme, ThemeProvider } from "@material-ui/core/styles";
import purple from "@material-ui/core/colors/purple";
import green from "@material-ui/core/colors/green";
import CssBaseline from "@material-ui/core/CssBaseline";
import Navbar from "./component/Navbar";
import Main from "./component/Main";

const baseTheme = createTheme({
  palette: {
    type: "dark",
    primary: {
      main: purple[500],
    },
    secondary: {
      main: green[500],
    },
    // text: {
    //   primary: "#ffffff",
    //   secondary: "#ffffff",
    //   // disabled: "#ffffff",
    //   // hint: "#ffffff",
    // },
  },
});

function App() {
  return (
    <Router>
      <div style={{ background: "#1f1A24", margin: 0, padding: 0 }}>
        <ThemeProvider theme={baseTheme}>
          <CssBaseline />

          <div
            style={{
              width: "100vw",
              height: "100vh",
              display: "flex",
              flexDirection: "column",
            }}
          >
            <Navbar />
            <div
              style={{
                flex: 1,
                display: "flex",
              }}
            >
              <Main />
            </div>
            {/* <Switch>
            <Route path="/chat">
              <Chat />
            </Route>
            <Route path="/">
              <Login />
            </Route>
          </Switch> */}
          </div>
        </ThemeProvider>
      </div>
    </Router>
  );
}

export default App;
