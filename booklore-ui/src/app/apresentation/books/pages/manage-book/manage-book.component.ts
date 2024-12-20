import { Component } from '@angular/core';
import { BookRequest } from '../../../../services/models/book-request';
import { BooksService } from '../../../../services/services';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-manage-book',

  templateUrl: './manage-book.component.html',
  styleUrl: './manage-book.component.css',
})
export class ManageBookComponent {
  constructor(
    private bookService: BooksService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}
  errorMsg: Array<string> = [];
  isOkay: boolean = true;
  selectedBookCover: any;
  selectedPicture: string | undefined;
  bookRequest: BookRequest = {
    title: '',
    authorName: '',
    isbn: '',
    synopsis: '',
  };

  ngOnInit(): void {
    const bookId = this.activatedRoute.snapshot.params['id'];
    if (bookId) {
      this.bookService
        .findBookById({
          id: bookId,
        })
        .subscribe({
          next: (book) => {
            this.bookRequest = {
              id: book.id,
              title: book.title as string,
              authorName: book.authorName as string,
              isbn: book.isbn as string,
              synopsis: book.synopsis as string,
              shareable: book.shareable,
            };
            console.log(book.cover);
            this.selectedBookCover = 'data:image/jpg;base64,' + book.cover;
          },
        });
    }
  }

  saveBook() {
    this.bookService
      .saveBook({
        body: this.bookRequest,
      })
      .subscribe({
        next: (bookId) => {
          this.bookService
            .uploadBookCover({
              id: bookId,
              body: {
                file: this.selectedBookCover,
              },
            })
            .subscribe({
              next: () => {
                this.router.navigate(['/books/my-books']);
              },
            });
        },
        error: (err) => {
          this.isOkay = false;
          console.log(err.error);
          this.errorMsg = err.error.validationErrors;
        },
      });
  }
  onFileSelected(event: any) {
    this.selectedBookCover = event.target.files[0];
    console.log(this.selectedBookCover);

    if (this.selectedBookCover) {
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedPicture = reader.result as string;
      };
      reader.readAsDataURL(this.selectedBookCover);
    }
  }
}
