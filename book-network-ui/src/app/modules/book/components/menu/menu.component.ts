import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-menu',
  standalone: false,
  
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit{

  ngOnInit(): void {
    const linkColour = document.querySelectorAll('.nav-link');
    linkColour.forEach(link => {
      if (window.location.href.endsWith(link.getAttribute('href') || '')) {
        link.classList.add('active');
      }
      link.addEventListener('click', () => {
        linkColour.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
      })
    });
  }

  logout(){
    localStorage.removeItem('token');
    window.location.reload();
  }
}