export class UserService {
    static apiUsers = "/api/users";

    constructor(header, token) {
        this.header = header;
        this.token = token;
    }


    async createUser(unm, pass) {

        return fetch(UserService.apiUsers + "/",
            {
                headers:
                    {
                        "Content-Type": "application/json",
                        [this.header]: this.token
                    },
                method: "POST",
                body: JSON.stringify({username: unm, password: pass})
            });

    }

    async updateUser(unm, pass) {

        return fetch(UserService.apiUsers + "/",
            {
                headers:
                    {
                        "Content-Type": "application/json",
                        [this.header]: this.token
                    },
                method: "PUT",
                body: JSON.stringify({username: unm, password: pass})
            });

    }

    async updateUser(unm, pass, uR, id) {

        return fetch(UserService.apiUsers + "/" + id,
            {
                headers:
                    {
                        "Content-Type": "application/json",
                        [this.header]: this.token
                    },
                method: "PUT",
                body: JSON.stringify({username: unm, password: pass, role: uR})
            });
    }

    async deleteUser(i) {
        return fetch(UserService.apiUsers + "/" + i, {
            method: "DELETE",
            headers: {
                'Content-Type': 'application/json',
                [this.header]: this.token  // Додавання CSRF-токену до заголовків
            }
        })
    }
}