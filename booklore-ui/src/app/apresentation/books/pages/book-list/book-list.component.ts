import { Component } from '@angular/core';
import { BooksService } from '../../../../services/services/books.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { findAllBooks } from '../../../../services/fn/books/find-all-books';
import { BookResponse, PageResponseBookResponse } from '../../../../services/models';

@Component({
  selector: 'app-book-list',

  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.css'
})
export class BookListComponent {
  bookResponse:PageResponseBookResponse = {}
  page = 0
  size = 5
  isOkay: boolean = true;
  submitted: boolean = false;
  message = ''
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
  borrowBook(book: BookResponse) {
    this.message = '';
    this.bookService.borrowBook({
      'id': book.id as number
    }).subscribe({
      next: () => {
        this.submitted = true;
        this.isOkay = true;
        this.message = 'Livro adicionado a sua lista com sucesso';
      },
      error: (err) => {
        console.log(err);
        this.isOkay = false;
        this.message = err.error.error;
      }
    });
  }

  displayBookDetails(book: BookResponse) {
    this.router.navigate(['books', 'details', book.id]);
  }
  findAllBooks() {
    this.bookService.findAllBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (response) => {
        console.log(response)

        this.message = 'Livro adicionado a sua lista com sucesso'
        this.bookResponse = response
        this.pages = Array(this.bookResponse.totalPages)
            .fill(0)
            .map((x, i) => i);


      },
      error: (error) => {
        this.isOkay = false;
        this.submitted = false;
        this.message = error.error.console.error;
        ;
      }
    })
  }
}
