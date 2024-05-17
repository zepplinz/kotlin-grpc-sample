import * as grpc from '@grpc/grpc-js';
import { KeyValueServiceClient } from './services/KeyValueService';

function main(args: string[]) {

    const channel = grpc.credentials.createInsecure();
    const kvsClient = new KeyValueServiceClient('localhost:15000', channel);

    const server = new grpc.Server();
    server.addService(UserService, { kvsClient });
    server.bindAsync('localhost:15001', grpc.ServerCredentials.createInsecure(), () => {
        server.start();
        console.log('User service started');
    });

    process.on('SIGINT', () => {
        console.log('Ups, Node.js shutdown');
        server.tryShutdown(() => {
            console.log('User service stopped');
        });
    });
}

main(process.argv);