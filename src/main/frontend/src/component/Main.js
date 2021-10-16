import React, { useState, useEffect, useRef } from "react";
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
import validator from "validator";
import useStyles from "../styles";
import axios from "axios";
import { Client, Message } from "@stomp/stompjs";
import moment from "moment";

const Main = () => {
  let classes = useStyles();
  const [username, setUsername] = useState("");
  const [serverUsername, setServerUsername] = useState("");
  const [language, setLanguage] = useState("en");
  const [loginFailedMessage, setLoginFailedMessage] = useState("");
  const [subscription, setSubscription] = useState(null);

  const [usernameError, setUsernameError] = useState("");
  const [availableLanguages, setAvailableLanguages] = useState([]);
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");

  const [users, setUsers] = useState([]);
  const [userColors, setUserColors] = useState({});
  const [client, setClient] = useState({});
  const [loading, setLoading] = useState(false);

  const getAvailableLanguages = async () => {
    try {
      console.log("getting languiages...");
      const response = await axios.get("/languages");
      console.log("Languages");
      console.log(response.data);
      setAvailableLanguages(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getAvailableLanguages();
    setClient(
      new Client({
        // Use WSS when deploying to Heroku. Also need to use the exact domain name
        brokerURL: "wss://localhost:8080/allspeak",
        // Use WS when testing locally
        // brokerURL: "ws://localhost:8080/allspeak",
        connectHeaders: {},
        debug: function (str) {
          console.log("STOMP_CLIENT: " + str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      })
    );
  }, []);

  const isValidated = () => {
    let isValid = true;

    // Reset Errors
    setUsernameError("");

    // Username can't be empty
    if (validator.isEmpty(username)) {
      setUsernameError("Please enter a username");
      isValid = false;
    }
    // Username must be alphanumeric
    else if (
      !validator.isAlphanumeric(username, "en-US", { ignore: " " }) ||
      !validator.isLength(username, { min: 1, max: 20 }) ||
      !validator.isAlphanumeric(username[0])
    ) {
      setUsernameError(
        "Username must be 1-20 characters long and contain only letters, numbers, and spaces and start with a letter or number"
      );
      isValid = false;
    }
    return isValid;
  };

  // TODO: handle different message types (CHAT, CONNECT, DISCONNECT)
  // CONNECT and DISCONNECT will come from public subscription
  // CHAT messages will come from language subscription
  const onMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);
    console.log("Message Received");
    console.log(message);
    setMessages((messages) => [...messages, message]);
  };

  const userUpdateReceived = (payload) => {
    const activeUsers = JSON.parse(payload.body);
    console.log("User List Update Received");
    console.log(activeUsers);
    setUsers(activeUsers);

    const userColorMap = {};
    for (var i = 0; i < activeUsers.length; i++) {
      userColorMap[activeUsers[i].username] = activeUsers[i].color;
    }
    setUserColors(userColorMap);
  };

  client.beforeConnect = function (frame) {
    console.log("Client starting to connect...");
  };

  client.onConnect = function (frame) {
    console.log("Client connected");
    // Do something, all subscribes must be done is this callback
    // This is needed because this will be executed after a (re)connect

    // Subscribe to the chosen language
    // Client.subscribe() returns an object that contains the subscription id "id" and a method unsubscribe()
    var languageSubscription = client.subscribe(
      "/topic/" + language,
      onMessageReceived
    );

    var publicSubscription = client.subscribe(
      "/topic/public",
      onMessageReceived
    );

    var usersSubscription = client.subscribe(
      "/topic/users",
      userUpdateReceived
    );

    setSubscription(languageSubscription);

    // Send message that user connected
    client.publish({
      destination: "/app/chat.newUser",
      body: JSON.stringify({ sender: username, type: "CONNECT" }),
    });
  };

  client.onStompError = function (frame) {
    // Will be invoked in case of error encountered at Broker
    // Bad login/passcode typically will cause an error
    // Complaint brokers will set `message` header with a brief message. Body may contain details.
    // Compliant brokers will terminate the connection after any error
    console.log("Broker reported error: " + frame.headers["message"]);
    console.log("Additional details: " + frame.body);
  };

  const handleLanguageChange = (event) => {
    setLanguage(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoginFailedMessage("");
    if (isValidated()) {
      // const isLoginSuccessful = await login(username, password);
      // if (!isLoginSuccessful)
      //   setLoginFailedMessage("Incorrect username and/or password");
      client.activate();
      setLoading(true);
    } else {
    }
  };

  const sendMessage = (event) => {
    event.preventDefault();
    if (text) {
      const chatMessage = {
        sender: username,
        content: text,
        type: "CHAT",
        time: moment().calendar(),
      };
      client.publish({
        destination: "/app/chat.send",
        body: JSON.stringify(chatMessage),
      });

      setText("");
    }
  };

  const containerRef = useRef(null);

  useEffect(() => {
    if (containerRef && containerRef.current) {
      const element = containerRef.current;
      element.scroll({
        top: element.scrollHeight,
        left: 0,
        behavior: "smooth",
      });
    }
  }, [containerRef, messages]);

  const messageList = messages.map((message, index) => (
    <div key={index} style={{ padding: 5 }}>
      {message.type === "CONNECT" || message.type === "DISCONNECT" ? (
        <div style={{ fontStyle: "italic" }}>{message.content}</div>
      ) : null}
      {message.type === "CHAT" ? (
        <div className={classes.messageContainer}>
          <div className={classes.messageSenderIconContainer}>
            <div
              className={classes.messageSenderIcon}
              style={{ backgroundColor: userColors[message.sender] }}
            >
              {message.sender.charAt(0)}
            </div>
          </div>
          <div className={classes.messageContent}>
            <div className={classes.messageIdentifier}>
              <div className={classes.messageSender}>{message.sender}</div>
              <div className={classes.messageTimeStamp}>{message.time}</div>
            </div>
            <div>{message.content}</div>
          </div>
        </div>
      ) : null}
    </div>
  ));

  const usersList = users.map((userElement, index) => (
    <div key={index} style={{ padding: 5 }}>
      <div className={classes.user}>
        <div
          className={classes.userIcon}
          style={{ backgroundColor: userColors[userElement.username] }}
        >
          {userElement.username.charAt(0)}
        </div>
        <div className={classes.username}>{userElement.username}</div>
      </div>
    </div>
  ));

  const handleTextChange = (event) => {
    setText(event.target.value);
  };

  if (subscription) {
    return (
      <Container component="main" maxWidth="md" classes={classes.mainContainer}>
        <Card classes={classes.mainContainer}>
          <CardContent className={classes.main}>
            <div className={classes.chat}>
              <div ref={containerRef} className={classes.chatArea}>
                {messageList}
              </div>
              <div>
                <form className={classes.chatEntry} onSubmit={sendMessage}>
                  <TextField
                    id="filled-multiline-static"
                    className={classes.chatField}
                    label="Message"
                    multiline
                    rows={3}
                    defaultValue=""
                    value={text}
                    variant="filled"
                    onChange={handleTextChange}
                  />
                  <Button type="submit" variant="contained" color="primary">
                    Submit
                  </Button>
                </form>
              </div>
            </div>

            <div className={classes.users}>
              <h4>Users</h4>
              <div className={classes.userList}>{usersList}</div>
            </div>
          </CardContent>
        </Card>
      </Container>
    );
  }

  return (
    <Container component="main" maxWidth="xs">
      <div className={classes.paper}>
        <h2 className={classes.cartHeaderTitle}>Login</h2>
        <form className={classes.form} noValidate>
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            id="username1"
            label="Username"
            name="username"
            onChange={(event) => {
              setUsername(event.target.value);
            }}
            onKeyPress={(e) => {
              if (e.key === "Enter") {
                console.log("Enter key pressed");
                // write your functionality here
              }
            }}
            autoFocus
            helperText={usernameError}
            error={usernameError ? true : false}
          />
          {availableLanguages.length > 0 ? (
            <FormControl fullWidth>
              <InputLabel id="demo-simple-select-label">Language</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={language}
                label="Language"
                onChange={handleLanguageChange}
              >
                {availableLanguages.map((language, index) => (
                  <MenuItem value={language.code} key={language.code}>
                    {language.name}
                  </MenuItem>
                ))}
              </Select>
              <Button
                onClick={handleSubmit}
                fullWidth
                variant="contained"
                color="primary"
                className={classes.submit}
              >
                Submit
              </Button>
              {loading ? (
                <Typography variant="subtitle2" className={classes.error}>
                  The server is full. Please try again later
                </Typography>
              ) : null}
              {loginFailedMessage ? (
                <Typography variant="subtitle2" className={classes.error}>
                  {loginFailedMessage}
                </Typography>
              ) : null}
            </FormControl>
          ) : (
            <Typography variant="subtitle2" className={classes.error}>
              The server is full. Try again later
            </Typography>
          )}
        </form>
      </div>
    </Container>
  );
};

export default Main;
