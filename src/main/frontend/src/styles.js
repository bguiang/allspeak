import { makeStyles } from "@material-ui/core/styles";

// style hook
// uses the theme provider https://material-ui.com/customization/theming/
const useStyles = makeStyles((theme) => ({
  container: {
    padding: 0,
  },
  background: {
    background: "#1f1A24",
  },
  navbar: {
    paddingTop: 50,
    paddingBottom: 20,
  },
  navbarTitle: {
    margin: 0,
    padding: 0,
    color: "white",
  },
  mainContainer: {
    flex: 1,
    display: "flex",
  },
  main: {
    flex: 1,
    display: "flex",
    height: 500,
  },
  chat: {
    flex: 1,
    display: "flex",
    flexDirection: "column",
  },
  chatEntry: {
    display: "flex",
    flexDirection: "row",
  },
  chatArea: {
    flex: 1,
    // overflow: "scroll",
    overflow: "auto",
    flexDirection: "column-reverse",
  },
  chatField: {
    flex: 1,
  },
  users: {
    paddingLeft: 5,
    paddingRight: 5,
    width: 270,
    display: "flex",
    flexDirection: "column",
    overflow: "auto",
  },
  userList: {
    flex: 1,
  },
  messageContainer: {
    display: "flex",
  },
  messageSenderIconContainer: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    padding: 5,
  },
  messageSenderIcon: {
    lineHeight: 0,
    width: 0,
    padding: 20,
    borderRadius: "50%",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    fontWeight: "bold",
    fontSize: "1.2em",
  },
  messageContent: {
    flex: 1,
    padding: 5,
  },
  messageIdentifier: {
    display: "flex",
  },
  messageSender: {
    fontWeight: "bold",
  },
  messageTimeStamp: {
    color: "#808080",
    marginLeft: 5,
    fontStyle: "italic",
  },

  user: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
  },
  userIcon: {
    lineHeight: 0,
    width: 0,
    padding: 20,
    borderRadius: "50%",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    fontWeight: "bold",
    fontSize: "1.2em",
  },
  username: {
    flex: 1,
    fontWeight: "bold",
    padding: 5,
  },
}));

export default useStyles;
