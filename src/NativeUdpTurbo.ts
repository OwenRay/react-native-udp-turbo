import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  // Socket creation and management
  createSocket(options: { type: 'udp4' | 'udp6' }): number;
  bind(
    id: number,
    port: number,
    address: string,
    options: Object
  ): Promise<void>;
  close(id: number): Promise<void>;

  // Data transmission
  sendBase64(
    id: number,
    base64String: string,
    port: number,
    address: string
  ): Promise<void>;

  // Data receive
  receive(id: number): Promise<string>;

  // Socket configuration
  setBroadcast(id: number, flag: boolean): Promise<void>;

  // Multicast operations
  addMembership(id: number, multicastAddress: string): Promise<void>;
  dropMembership(id: number, multicastAddress: string): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('UdpTurbo');
