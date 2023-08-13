export class BookService {

    static apiBooks = "/api/books";

    constructor(header, token) {
        this.header = header;
        this.token = token;
    }

    async addBook(bookUrl) {
        // const token = $("meta[name='_csrf']").attr("content");
        // const header = $("meta[name='_csrf_header']").attr("content");


        // console.log(url + '?' + bookUrl.name + '=' + bookUrl.value);
        console.log(bookUrl.name, bookUrl.value)
        return fetch(BookService.apiBooks + '/?' + bookUrl.name + '=' + bookUrl.value, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                [this.header]: this.token  // Додавання CSRF-токену до заголовків
            }
        });
    }


    async createBook(title, description, bookImg) {

        // const token = $("meta[name='_csrf']").attr("content");
        // const header = $("meta[name='_csrf_header']").attr("content");

        return fetch(BookService.apiBooks + "/new", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                [this.header]: this.token  // Додавання CSRF-токену до заголовків
            },

            body: JSON.stringify({title, description, bookImage: bookImg})
        })
    }

    async updateBook(bD, title, description, bookImg) {
        return fetch(BookService.apiBooks + "/" + bD, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json',
                [this.header]: this.token  // Додавання CSRF-токену до заголовків
            },
            body: JSON.stringify({title, description, bookImage: bookImg})
        })


    }


    async deleteBook(id) {
        return fetch(BookService.apiBooks + "/" + id,
            {
                method: "DELETE",
                headers: {
                    'Content-Type': 'application/json',
                    [this.header]: this.token  // Додавання CSRF-токену до заголовків
                }
            })

    }


}