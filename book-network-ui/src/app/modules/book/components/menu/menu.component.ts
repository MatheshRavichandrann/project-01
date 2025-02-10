import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../../services/services/user.service';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
  username: string = 'Guest';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    const linkColour = document.querySelectorAll('.nav-link');
    linkColour.forEach(link => {
      if (window.location.href.endsWith(link.getAttribute('href') || '')) {
        link.classList.add('active');
      }
      link.addEventListener('click', () => {
        linkColour.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
      });
    });

    // Fetch user info on initialization
    this.userService.getUserInfo().subscribe(
      (data) => {
        this.username = data.username;
      },
      (error) => {
        console.error('Error fetching user info:', error);
      }
    );
  }

  logout() {
    localStorage.removeItem('token');
    window.location.reload();
  }
}
