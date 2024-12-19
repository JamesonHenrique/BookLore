import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/services';
import { CodeInputComponent, CodeInputModule } from 'angular-code-input';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-activate-account',
  
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class ActivateAccountComponent {
  constructor(  private router: Router,
    private authService: AuthenticationService
 ) {}
  isOkay: boolean = true;
  submitted: boolean = false;
  message: string = '';
  activate() {}

  redirectToLogin() {
    this.router.navigate(['login']);
  }
  onCodeCompleted(event: String) {

    this.authService.confirm({
      token: event as string
    }).subscribe({
      next: (response) => {
        this.message = 'Sua conta foi ativada com sucesso.\nAgora você pode prosseguir para o login';


        this.submitted = true;

      },
      error: (error) => {
        this.message = 'O token expirou ou é inválido';
        this.isOkay = false;
      }
    });
  }

}
