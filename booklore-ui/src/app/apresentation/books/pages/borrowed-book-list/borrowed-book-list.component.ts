import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  BookResponse,
  BorrowedBookResponse,
  FeedbackRequest,
  PageResponseBorrowedBookResponse,
} from '../../../../services/models';
import { BooksService, FeedbackService } from '../../../../services/services';

@Component({
  selector: 'app-borrowed-book-list',

  templateUrl: './borrowed-book-list.component.html',
  styleUrl: './borrowed-book-list.component.css',
})
export class BorrowedBookListComponent {
  constructor(
    private bookService: BooksService,
    private feedbackService: FeedbackService
  ) {}
  page = 0;
  size = 2;
  pages: any = [];
  borrowedBooks: PageResponseBorrowedBookResponse = {};
  selectedBook: BookResponse | undefined = undefined;
  feedbackRequest: FeedbackRequest = { bookId: 0, comment: '', note: 0 };

  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }
  returnBorrowedBook(book: BorrowedBookResponse) {
    this.selectedBook = book;
    this.feedbackRequest.bookId = book.id as number;
  }

  returnBook(withFeedback: boolean) {
    this.bookService
      .returnBorrowBook({
        id: this.selectedBook?.id as number,
      })
      .subscribe({
        next: () => {
          if (withFeedback) {
            this.giveFeedback();
          }
          this.selectedBook = undefined;
          this.findAllBorrowedBooks();
        },
      });
  }

  private giveFeedback() {
    this.feedbackService
      .saveFeedback({
        body: this.feedbackRequest,
      })
      .subscribe({
        next: () => {},
      });
  }

  private findAllBorrowedBooks() {
    this.bookService
      .findAllBorrowedBooks({
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: (resp) => {
          this.borrowedBooks = resp;
          this.pages = Array(this.borrowedBooks.totalPages)
            .fill(0)
            .map((x, i) => i);
        },
      });
  }
  gotToPage(page: number) {
    this.page = page;
    this.findAllBorrowedBooks();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBorrowedBooks();
  }

  goToPreviousPage() {
    if (this.page > 0) {
      this.page--;
    }
    this.findAllBorrowedBooks();
  }

  goToLastPage() {
    this.page = (this.borrowedBooks.totalPages as number) - 1;
    this.findAllBorrowedBooks();
  }

  goToNextPage() {
    if (this.page < (this.borrowedBooks.totalPages ?? 0) - 1) {
      this.page++;
    }
    this.findAllBorrowedBooks();
  }

  get isLastPage() {
    return this.page === (this.borrowedBooks.totalPages as number) - 1;
  }
  @Input() rating: number = 0;

  @Output() ratingClicked: EventEmitter<number> = new EventEmitter<number>();
  maxRating: number = 5;

  get fullStars(): number {
    return Math.floor(this.rating);
  }

  get hasHalfStar(): boolean {
    return this.rating % 1 !== 0;
  }

  get emptyStars(): number {
    return this.maxRating - Math.ceil(this.rating);
  }
}
