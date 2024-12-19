import { Component } from '@angular/core';
import { BookResponse, PageResponseBookResponse } from '../../../../services/models';
import { Router } from '@angular/router';
import { BooksService } from '../../../../services/services';

@Component({
  selector: 'app-my-books',

  templateUrl: './my-books.component.html',
  styleUrl: './my-books.component.css'
})
export class MyBooksComponent {
  bookResponse:PageResponseBookResponse = {}
  page = 0
  size = 5

  pages: any = [];
  constructor(private bookService: BooksService,
    private router: Router
  ) { }
  error:boolean = false
  ngOnInit(): void {
    this.findAllBooks()
  }
  gotToPage(page: number) {
    this.page = page;
    this.findAllBooks();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBooks();
  }

  goToPreviousPage() {
    if(this.page > 0){
      this.page--;
    }
    this.findAllBooks();
  }

  goToLastPage() {
    this.page = this.bookResponse.totalPages as number - 1;
    this.findAllBooks();
  }

  goToNextPage() {
    if (this.page < (this.bookResponse.totalPages ?? 0) - 1) {
      this.page++;
    }
    this.findAllBooks();
  }

  get isLastPage() {
    return this.page === this.bookResponse.totalPages as number - 1;
  }


  displayBookDetails(book: BookResponse) {
    this.router.navigate(['books', 'details', book.id]);
  }
  findAllBooks() {
    this.bookService.findAllBooksByOwner({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (response) => {
        console.log(response)


        this.bookResponse = response
        this.pages = Array(this.bookResponse.totalPages)
            .fill(0)
            .map((x, i) => i);


      },
      error: (error) => {

        ;
      }
    })
  }

  archiveBook(book: BookResponse) {
    this.bookService.updateArchivedStatus({
      'id': book.id as number
    }).subscribe({
      next: () => {
        book.archived = !book.archived;
      }
    });
  }

  shareBook(book: BookResponse) {
    this.bookService.updateShareableStatus({
      'id': book.id as number
    }).subscribe({
      next: () => {
        book.shareable = !book.shareable;
      }
    });
  }

  editBook(book: BookResponse) {
    this.router.navigate(['books', 'manage', book.id]);
  }
}
