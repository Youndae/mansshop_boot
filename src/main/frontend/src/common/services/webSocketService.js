import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { axiosEnhanced } from '../utils/axios/axiosEnhanced';

import { getToken } from "../utils/axios/tokenUtils";

let stompClient = null;
let connected = false;
let heartbeatInterval = null;

const connect = (userId, onNotificationReceived, retryCount = 0) => {
	const socket = new SockJS('/ws');
	stompClient = Stomp.over(() => socket);

	const token = getToken();
	const connectHeaders = {};
	if(token) {
		console.log('token : ', token);
		connectHeaders['Authorization'] = token;
	}

	stompClient.heartbeat.outgoing = 20000;
	stompClient.heartbeat.incoming = 20000;
	stompClient.reconnectDelay = 5000;
	console.log('connect console');

	stompClient.connect(connectHeaders, (frame) => {
		console.log('Connected : ' + frame);
		connected = true;

		//개인 알림 구독
		stompClient.subscribe('/user/queue/notifications', (message) => {
			const notification = JSON.parse(message.body);
			onNotificationReceived(notification);
		});

		//30초 간격 Heartbeat
		startHeartbeat();
	}, (error) => {
		console.error('WebSocket connection error : ', error);
		connected = false;

		if(retryCount < 3) {
			setTimeout(() => {
				console.log(`Retrying WebSocket connection ( ${retryCount + 1} / 3 )`);
				connect(userId, onNotificationReceived, retryCount + 1);
			}, 5000);
		}
	});
};

const startHeartbeat = () => {
	heartbeatInterval = setInterval(() => {
		if(connected && stompClient) {
			axiosEnhanced.get('/notification/heartbeat')
				.catch(error => {
					console.error('Heartbeat failed : ', error);
				});
		}
	}, 30000);
};

const disconnect = () => {
	if(heartbeatInterval) {
		clearInterval(heartbeatInterval);
		heartbeatInterval = null;
	}

	if(stompClient !== null) {
		stompClient.disconnect();
		connected = false;
	}

	console.log('Disconnected');
};

const isConnected = () => {
	return connected;
}

export {connect, disconnect, isConnected };