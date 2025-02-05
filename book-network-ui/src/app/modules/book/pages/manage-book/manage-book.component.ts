import { Component, OnInit } from '@angular/core';
import { BookRequest } from '../../../../services/models';
import { BookService } from '../../../../services/services';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-manage-book',
  standalone: false,
  templateUrl: './manage-book.component.html',
  styleUrls: ['./manage-book.component.scss'] // Fixed typo
})
export class ManageBookComponent implements OnInit {
  bookRequest: BookRequest = { authorName: '', isbn: '', synopsis: '', title: '' };
  errorMsg: Array<string> = [];
  selectedBookCover: any = null; // Initialize as null
  selectedPicture: string | undefined;

  constructor(
    private bookService: BookService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const bookId = this.activatedRoute.snapshot.params['bookId'];
    if (bookId) {
      this.bookService.findBookById({ 'book-id': bookId }).subscribe({
        next: (book) => {
          this.bookRequest = {
            id: book.id,
            title: book.title as string,
            authorName: book.authorName as string,
            isbn: book.isbn as string,
            synopsis: book.synopsis as string,
            sharable: book.sharable
          };
          if (book.cover) {
            this.selectedPicture = 'data:image/jpg;base64,' + book.cover;
          }
        },
        error: (err) => {
          this.errorMsg = ['An error occurred while fetching book details.'];
        }
      });
    }
  }

  onFileSelected(event: any): void {
    this.selectedBookCover = event.target.files[0];
    console.log(this.selectedBookCover);
    if (this.selectedBookCover) {
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedPicture = reader.result as string;
      };
      reader.readAsDataURL(this.selectedBookCover);
    }
  }

  saveBook(): void {
    this.bookService.saveBook({ body: this.bookRequest }).subscribe({
      next: (bookId) => {
        // Check if a new file is selected
        if (this.selectedBookCover) {
          // If a new file is selected, upload it
          this.bookService.uploadBookCoverPicture({
            'book-id': bookId,
            body: {
              file: this.selectedBookCover
            }
          }).subscribe({
            next: () => {
              this.router.navigate(['/books/my-books']);
            },
            error: (err) => {
              this.errorMsg = ['An error occurred while uploading the book cover.'];
            }
          });
        } else {
          // No new file, just navigate
          this.router.navigate(['/books/my-books']);
        }
      },
      error: (err) => {
        this.errorMsg = err.error.validationErrors || ['An error occurred while saving the book.'];
      }
    });
  }
}
