export class CatalogService {
    static apiCatalogs = "/api/catalogs";


    constructor(header, token) {
        this.header = header;
        this.token = token;
    }

    async createCatalog(title, isPublic) {
        return fetch(CatalogService.apiCatalogs + "/",
            {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                    [this.header]: this.token  // Додавання CSRF-токену до заголовків
                },
                body: JSON.stringify({title, isPublic})
            });
    }

    async getCatalog(cD) {
        return fetch(CatalogService.apiCatalogs + "/" + cD,
            {
                method: "GET",
                headers:
                    {
                        'Content-Type': 'application/json',
                        [this.header]: this.token  // Додавання CSRF-токену до заголовків
                    }
            });
    }

    async updateCatalog(cD, title, isPublic) {

        return fetch(CatalogService.apiCatalogs + "/" + cD,
            {
                method: "PUT",
                headers: {
                    'Content-Type': 'application/json',
                    [this.header]: this.token  // Додавання CSRF-токену до заголовків
                },
                body: JSON.stringify({title, isPublic})
            });
    }

    async deleteCatalog(cD) {

        return fetch("/api/catalogs/" + cD,
            {
                method: "DELETE",
                headers: {
                    'Content-Type': 'application/json',
                    [this.header]: this.token  // Додавання CSRF-токену до заголовків
                }
            });
    }
}