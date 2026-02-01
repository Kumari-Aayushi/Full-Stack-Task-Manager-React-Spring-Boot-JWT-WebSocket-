import SockJS from "sockjs-client";
import Stomp from "stompjs";

export const connectSocket = (onMessage: (data: any) => void) => {
  const socket = new SockJS("http://localhost:8080/ws");
  const stompClient = Stomp.over(socket);

  stompClient.connect({}, () => {
    stompClient.subscribe("/topic/tasks", (message) => {
      onMessage(JSON.parse(message.body));
    });
  });

  return stompClient;
};
