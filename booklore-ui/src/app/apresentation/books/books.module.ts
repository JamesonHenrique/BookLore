import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookRoutingModule } from './books.routing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { BooksService } from '../../services/services';
import { FormsModule } from '@angular/forms';
import { ReturnedBooksComponent } from './pages/returned-books/returned-books.component';
import { MenuComponent } from './components/menu/menu.component';
import { BookListComponent } from './pages/book-list/book-list.component';
import { MainComponent } from './pages/main/main.component';
import { MyBooksComponent } from './pages/my-books/my-books.component';
import { BorrowedBookListComponent } from './pages/borrowed-book-list/borrowed-book-list.component';
import { BookCardComponent } from './components/book-card/book-card.component';
import { RatingComponent } from './components/raiting/rating.component';
import { ManageBookComponent } from './pages/manage-book/manage-book.component';

@NgModule({
  declarations: [
    MainComponent,
    MenuComponent,
    BookListComponent,
    MyBooksComponent,
    BorrowedBookListComponent,
    ReturnedBooksComponent,
    BookCardComponent,
    RatingComponent,
    ManageBookComponent,
  ],
  imports: [CommonModule, BookRoutingModule, FormsModule],
})
export class BookModule {}
