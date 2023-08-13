export class ChapterService {
    static APICHAPTERS = "/api/chapters";


    constructor(header, token) {
        this.header = header;
        this.token = token;
    }


    createChapter(bD, title, text, numberOfPage) {
        return fetch(ChapterService.APICHAPTERS + '/book/' + bD + '/add',
            {
                method:"POST",
                headers: {
                    'Content-Type': 'application/json',
                    [this.header]: this.token  // Додавання CSRF-токену до заголовків
                },

                body: JSON.stringify({title, text, numberOfPage})
            })
    }

    updateChapter(bD, cD, title, text, numberOfPage) {
        return fetch(ChapterService.APICHAPTERS + '/book/' + bD + '/chapter/' + cD,
            {
                method:"PUT",
                headers: {
                    'Content-Type': 'application/json',
                    [this.header]: this.token  // Додавання CSRF-токену до заголовків
                },

                body: JSON.stringify({title, text, numberOfPage})
            });
    }

    deleteChapter(cD) {
        return fetch( ChapterService.APICHAPTERS + "/" + cD,
            {
                method: "DELETE",
                headers: {
                    'Content-Type': 'application/json',
                    [this.header]: this.token  // Додавання CSRF-токену до заголовків
                }
            })
    }

    reloadChapters(bD) {
        return fetch(ChapterService.APICHAPTERS + "/book/" + bD + "/reload", {
            headers: {
                'Content-Type': 'application/json',
                [this.header]: this.token  // Додавання CSRF-токену до заголовків
            }
        })
    }

}