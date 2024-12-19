import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MainComponent } from './pages/main/main.component';
import { BookListComponent } from './pages/book-list/book-list.component';
import { MyBooksComponent } from './pages/my-books/my-books.component';
import { BorrowedBookListComponent } from './pages/borrowed-book-list/borrowed-book-list.component';
import { ReturnedBooksComponent } from './pages/returned-books/returned-books.component';
import { ManageBookComponent } from './pages/manage-book/manage-book.component';
import { AuthGuard } from '../../services/guard/guard.service';

const routes: Routes = [
  {
    path: '',
     component: MainComponent,

    children:
     [
      {
        path: '',
        component: BookListComponent,
        canActivateChild: [AuthGuard]

      },
      {
        path: 'my-books',
        component: MyBooksComponent,
        canActivateChild: [AuthGuard]

      },
      {
        path: 'my-borrowed-books',
        component: BorrowedBookListComponent,
        canActivateChild: [AuthGuard]

      },

      {
        path: 'my-returned-books',
        component: ReturnedBooksComponent,
        canActivateChild: [AuthGuard]

      },
      {
        path: 'manage/:id',
        component: ManageBookComponent,
        canActivateChild: [AuthGuard]

      },
      {
        path: 'manage',
        component: ManageBookComponent,
        canActivateChild: [AuthGuard]

      }],
   },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BookRoutingModule {
}
