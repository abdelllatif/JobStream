# Angular AI Prompt — Password Management (Change / Set Password)

## Context

The backend has three relevant endpoints for password management:

| Method | URL | Auth | Purpose |
|---|---|---|---|
| `GET` | `/api/users/me/has-password` | Bearer JWT | Returns `true` if the current user has a password set, `false` if they signed in only via Google |
| `POST` | `/api/users/me/set-password` | Bearer JWT | For Google-only users: sets a new password and upgrades their provider to `LOCAL_GOOGLE` |
| `PUT` | `/api/users/me/change-password` | Bearer JWT | For users who already have a password: changes it (requires current password) |

---

## User Flows

### Flow A — User has NO password (Google-only account)
1. Component loads → calls `GET /api/users/me/has-password` → receives `false`
2. Show a **Set Password** form:
   - **New password** (required, min 8 chars)
   - **Confirm new password** (required, must match)
3. On submit → `POST /api/users/me/set-password`
4. Show success message on 200

### Flow B — User HAS a password
1. Component loads → calls `GET /api/users/me/has-password` → receives `true`
2. Show a **Change Password** form:
   - **Current password** (required)
   - **New password** (required, min 8 chars)
   - **Confirm new password** (required, must match)
3. On submit → `PUT /api/users/me/change-password`
4. Show success message on 200

---

## Request / Response Shapes

### `GET /api/users/me/has-password`
- **Response body**: plain JSON boolean — `true` or `false`
- No request body

### `POST /api/users/me/set-password`
```json
{
  "newPassword": "MyStr0ngPass!",
  "confirmedPassword": "MyStr0ngPass!"
}
```
- `200 OK` — empty body on success
- `400 Bad Request` — passwords don't match, or user already has a password

### `PUT /api/users/me/change-password`
```json
{
  "currentPassword": "OldPassword1!",
  "newPassword": "NewPassword2!",
  "confirmedPassword": "NewPassword2!"
}
```
- `200 OK` — empty body on success
- `400 Bad Request` — current password wrong, passwords don't match

---

## Angular Implementation

### 1. Service — `password.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SetPasswordPayload {
  newPassword: string;
  confirmedPassword: string;
}

export interface ChangePasswordPayload {
  currentPassword: string;
  newPassword: string;
  confirmedPassword: string;
}

@Injectable({ providedIn: 'root' })
export class PasswordService {
  private base = '/api/users/me';

  constructor(private http: HttpClient) {}

  hasPassword(): Observable<boolean> {
    return this.http.get<boolean>(`${this.base}/has-password`);
  }

  setPassword(payload: SetPasswordPayload): Observable<void> {
    return this.http.post<void>(`${this.base}/set-password`, payload);
  }

  changePassword(payload: ChangePasswordPayload): Observable<void> {
    return this.http.put<void>(`${this.base}/change-password`, payload);
  }
}
```

### 2. Component — `password-settings.component.ts`

```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { PasswordService } from './password.service';

@Component({
  selector: 'app-password-settings',
  templateUrl: './password-settings.component.html',
})
export class PasswordSettingsComponent implements OnInit {
  hasPassword: boolean | null = null;
  loading = false;
  successMessage = '';
  errorMessage = '';
  form!: FormGroup;

  constructor(private fb: FormBuilder, private passwordService: PasswordService) {}

  ngOnInit(): void {
    this.passwordService.hasPassword().subscribe({
      next: (result) => {
        this.hasPassword = result;
        this.buildForm(result);
      },
      error: () => {
        this.errorMessage = 'Failed to load password settings.';
      }
    });
  }

  private buildForm(hasPassword: boolean): void {
    if (hasPassword) {
      // Change password — needs current password
      this.form = this.fb.group(
        {
          currentPassword: ['', Validators.required],
          newPassword: ['', [Validators.required, Validators.minLength(8)]],
          confirmedPassword: ['', Validators.required],
        },
        { validators: this.passwordsMatch('newPassword', 'confirmedPassword') }
      );
    } else {
      // Set password — no current password needed
      this.form = this.fb.group(
        {
          newPassword: ['', [Validators.required, Validators.minLength(8)]],
          confirmedPassword: ['', Validators.required],
        },
        { validators: this.passwordsMatch('newPassword', 'confirmedPassword') }
      );
    }
  }

  private passwordsMatch(pass: string, confirm: string) {
    return (group: AbstractControl) => {
      const p = group.get(pass)?.value;
      const c = group.get(confirm)?.value;
      return p && c && p !== c ? { passwordsMismatch: true } : null;
    };
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const { newPassword, confirmedPassword, currentPassword } = this.form.value;

    const request$ = this.hasPassword
      ? this.passwordService.changePassword({ currentPassword, newPassword, confirmedPassword })
      : this.passwordService.setPassword({ newPassword, confirmedPassword });

    request$.subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = this.hasPassword
          ? 'Password changed successfully.'
          : 'Password set successfully. You can now log in with your email and password.';
        this.hasPassword = true; // Now they have a password
        this.buildForm(true);   // Rebuild to show change-password form next time
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || 'Something went wrong. Please try again.';
      }
    });
  }

  get f() { return this.form.controls; }
}
```

### 3. Template — `password-settings.component.html`

```html
<div class="password-settings" *ngIf="hasPassword !== null; else loadingTpl">

  <h3>{{ hasPassword ? 'Change Password' : 'Set Password' }}</h3>

  <p *ngIf="!hasPassword" class="info-text">
    Your account was created via Google. Set a password to also be able to log in with your email.
  </p>

  <form [formGroup]="form" (ngSubmit)="onSubmit()">

    <!-- Current password — only shown when user already has one -->
    <div *ngIf="hasPassword" class="field">
      <label for="currentPassword">Current Password</label>
      <input id="currentPassword" type="password" formControlName="currentPassword" />
      <span *ngIf="f['currentPassword'].touched && f['currentPassword'].hasError('required')"
            class="error">Current password is required.</span>
    </div>

    <!-- New password -->
    <div class="field">
      <label for="newPassword">New Password</label>
      <input id="newPassword" type="password" formControlName="newPassword" />
      <span *ngIf="f['newPassword'].touched && f['newPassword'].hasError('required')"
            class="error">New password is required.</span>
      <span *ngIf="f['newPassword'].touched && f['newPassword'].hasError('minlength')"
            class="error">Password must be at least 8 characters.</span>
    </div>

    <!-- Confirm password -->
    <div class="field">
      <label for="confirmedPassword">Confirm New Password</label>
      <input id="confirmedPassword" type="password" formControlName="confirmedPassword" />
      <span *ngIf="f['confirmedPassword'].touched && f['confirmedPassword'].hasError('required')"
            class="error">Please confirm your new password.</span>
      <span *ngIf="form.hasError('passwordsMismatch') && f['confirmedPassword'].touched"
            class="error">Passwords do not match.</span>
    </div>

    <!-- Server messages -->
    <p class="success" *ngIf="successMessage">{{ successMessage }}</p>
    <p class="error"   *ngIf="errorMessage">{{ errorMessage }}</p>

    <button type="submit" [disabled]="form.invalid || loading">
      {{ loading ? 'Saving...' : (hasPassword ? 'Change Password' : 'Set Password') }}
    </button>

  </form>
</div>

<ng-template #loadingTpl>
  <p>Loading...</p>
</ng-template>
```

---

## Error Handling Quick Reference

| HTTP status | When | What to show |
|---|---|---|
| `400` | Passwords don't match (server-side) | `err.error.message` |
| `400` | Wrong current password | `err.error.message` |
| `400` | Already has password (called set-password by mistake) | `err.error.message` |
| `401` | Token expired | Redirect to login |
| `0` / network error | Server unreachable | Generic "Please try again" |

---

## Important Notes

- Always call `GET /api/users/me/has-password` on component init to decide which form to render — **never guess based on stored user profile data**.
- Client-side password match validation should mirror server-side; send the request only after client passes.
- After a successful `POST /api/users/me/set-password`, the user's provider becomes `LOCAL_GOOGLE` on the backend — no frontend action needed for this.
- `confirmedPassword` — the field name the backend expects exactly. Do **not** rename it to `confirmPassword`.
