import { Text, View, StyleSheet, Button, ScrollView } from 'react-native';
import UdpTurbo from 'react-native-udp-turbo';
import { Buffer } from 'buffer';
import { useCallback, useEffect, useMemo, useState } from 'react';

const PING_MESSAGE = Buffer.from('ping', 'ascii').toString('base64');
const PONG_MESSAGE = Buffer.from('pong', 'ascii').toString('base64');
const PING_PORT = 1235;
const PONG_PORT = 1234;
const BIND_ADDRESS = '0.0.0.0';

export default function App() {
  const idPing = useMemo(() => UdpTurbo.createSocket({ type: 'udp4' }), []);
  const idPong = useMemo(() => UdpTurbo.createSocket({ type: 'udp4' }), []);
  const [messages, setMessages] = useState<string[]>([]);

  const receivePing = useCallback(() => {
    UdpTurbo.receive(idPing)
      .then((message) => {
        console.log('received message', message);
        setMessages((currentMessages) => [
          ...currentMessages,
          Buffer.from(message, 'base64').toString('ascii'),
        ]);
      })
      .finally(receivePing);
  }, [setMessages, idPing]);

  const receivePong = useCallback(() => {
    UdpTurbo.receive(idPong)
      .then((message) => {
        console.log('received message', message);
        setMessages((currentMessages) => [
          ...currentMessages,
          Buffer.from(message, 'base64').toString('ascii'),
        ]);
      })
      .finally(receivePong);
  }, [setMessages, idPong]);

  useEffect(() => {
    console.log('bind addresses');
    UdpTurbo.bind(idPing, PING_PORT, BIND_ADDRESS, {})
      .then(() => console.log('bound ping'))
      .catch(console.error);
    UdpTurbo.bind(idPong, PONG_PORT, BIND_ADDRESS, {})
      .then(() => console.log('bound pong'))
      .catch(console.error);
    receivePing();
    receivePong();
  }, [idPing, idPong, receivePing, receivePong]);

  return (
    <View style={styles.container}>
      <Text>Click ping or pong</Text>
      <Button
        title={'Ping'}
        onPress={() => {
          console.log(
            'send ping',
            idPing,
            PING_MESSAGE,
            PONG_PORT,
            BIND_ADDRESS
          );
          UdpTurbo.sendBase64(idPing, PING_MESSAGE, PONG_PORT, '127.0.0.1')
            .then(() => console.log('sent ping'))
            .catch(console.error);
        }}
      />
      <Button
        title={'Poing'}
        onPress={() => {
          console.log('send pong');
          UdpTurbo.sendBase64(idPong, PONG_MESSAGE, PING_PORT, '127.0.0.1')
            .then(() => console.log('sent pong'))
            .catch(console.error);
        }}
      />
      <ScrollView>
        <Text>{messages.join('\n')}</Text>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
