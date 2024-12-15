import { AuthenticationService } from './../../services/services/authentication.service';
import { Component } from '@angular/core';
import { AuthenticationRequest } from '../../services/models';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { register } from '../../services/fn/authentication/register';
import { Router, RouterLink } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TokenService } from '../../services/token/token.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterLink,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private tokenService:TokenService
  ) {}
  authRequest: AuthenticationRequest = {
    email: '',
    password: '',
  };
  errorMsg: Array<string> = [];
  login() {
    this.errorMsg = [];
    this.authService
      .authenticate({
        body: this.authRequest,
      })
      .subscribe({
        next: (response) => {

          this.tokenService.token = response.token as string;
          this.router.navigate(['books']);
        },
        error: (error) => {
       
          if(error.error.validationErrors) {
         this.errorMsg = error.error.validationErrors;
          }
          this.errorMsg.push(error.error.error);
        }
      });
  }

}
