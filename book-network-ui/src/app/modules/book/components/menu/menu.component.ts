
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';
import { UserService } from '../../../../services/services/user.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  standalone: false,
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
  username: string = 'Guest';
  searchQuery: string = '';
  suggestions: string[] = [];
  showDropdown: boolean = false;
  private searchSubject = new Subject<string>();
  token = localStorage.getItem('token'); // Assume token is stored in localStorage

  constructor(private http: HttpClient, private userService: UserService) {}


  ngOnInit(): void {
    // Highlight active navigation link
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

    // Handle user search input with debounce
    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(query => this.fetchSuggestions(query))
    ).subscribe(suggestions => {
      this.suggestions = suggestions;
      this.showDropdown = suggestions.length > 0;
    });
  }

  onSearchInput(event: Event) {
    const inputElement = event.target as HTMLInputElement; // Typecast to HTMLInputElement
    const query = inputElement?.value.trim();

    this.searchQuery = query;

    if (query.length > 0) {
      this.searchSubject.next(query);
    } else {
      this.suggestions = [];
      this.showDropdown = false;
    }
  }

  hideDropdown() {
    setTimeout(() => {
      this.showDropdown = false;
    }, 200); // Short delay to allow click event to register
  }


  fetchSuggestions(query: string): Observable<string[]> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.token}` });
    return this.http.get<string[]>(`https://project-01-gm0t.onrender.com/api/v1/books/suggestions?keyword=${query}`, { headers });
  }

  selectSuggestion(value: string) {
    this.searchQuery = value;
    this.suggestions = [];
    this.showDropdown = false;
    this.searchBooks(value);
  }

  searchBooks(query: string) {
    if (!query.trim()) return;

    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.token}`,
      'Content-Type': 'application/json'
    });

    this.http.get<any[]>(`https://project-01-gm0t.onrender.com/api/v1/books/suggestions?keyword=${query}`, { headers })
      .subscribe(response => {
        if (response.length > 0) {
          if (response[0].bookId) {
            console.log('Books:', response);
          } else if (response[0].category) {
            window.location.href = `/menu.html?f=${encodeURIComponent(btoa(JSON.stringify(response)))}`;
          }
        }
      }, error => {
        console.error('Something went wrong', error);
      });
  }

  handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && this.searchQuery.trim()) {
      this.searchBooks(this.searchQuery);
    }
  }

  logout() {
    localStorage.removeItem('token');
    window.location.reload();
  }
}
