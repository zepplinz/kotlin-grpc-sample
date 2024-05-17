import { GetRequest, KeyValueServiceClient, UserRequest, UserResponse, UserServiceService } from 'services';
import { sendUnaryData, ServerUnaryCall } from 'grpc';

class UserService implements UserServiceService {
    private keyValue: KeyValueServiceClient;

    constructor(keyValue: KeyValueServiceClient) {
        this.keyValue = keyValue;
    }

    async getUser(call: ServerUnaryCall<UserRequest>, callback: sendUnaryData<UserResponse>): Promise<void> {
        const getValue = async (key: string): Promise<string> => {
            return new Promise((resolve, reject) => {
                this.keyValue.get(
                    new GetRequest().setKey(call.request.getName() + key),
                    (err, response) => {
                        if (err) {
                            reject(new Error('key not found'));
                        } else {
                            resolve(response.getValue());
                        }
                    }
                );
            });
        };

        try {
            const email = getValue('.email');
            const country = getValue('.country');
            const active = getValue('.active');

            const userResponse = new UserResponse()
                .setName(call.request.getName() || (() => { throw new Error('name can not be null'); })())
                .setEmailAddress(await email)
                .setCountry(await country)
                .setActive((await active).toLowerCase() === 'true');

            callback(null, userResponse);
        } catch (error) {
            callback(error, null);
        }
    }
}

export { UserService };