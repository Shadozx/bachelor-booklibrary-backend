export class BookMarkService {
    static apiBookMarks = "/api/bookmarks";


    constructor(header, token) {
        this.header = header;
        this.token = token;
    }


    async createMark(cD, bD) {
        return fetch(BookMarkService.apiBookMarks + "/catalog/" + cD + "/book/" + bD,
            {
                method: "POST",
                headers:
                    {
                        'Content-Type': 'application/json',
                        [this.header]: this.token  // Додавання CSRF-токену до заголовків
                    }
            });
    }

    async updateBookMark(cD, par) {

        const params = this.buildQueryString({ch: cD, par});

        return fetch("/api/bookmarks/" + "?" + params,
            {
                method: "PUT",
                headers:
                    {
                        'Content-Type': 'application/json',
                        [this.header]: this.token  // Додавання CSRF-токену до заголовків
                    }
            })
    }


    async deleteMark(id) {
        return fetch(BookMarkService.apiBookMarks + "/" + id,
            {
                method: "DELETE",
                headers: {
                    'Content-Type': 'application/json',
                    [this.header]: this.token  // Додавання CSRF-токену до заголовків
                }
            });
    }


    buildQueryString(params) {
        const queryString = Object.keys(params).map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`).join('&');
        return queryString;
    }
}