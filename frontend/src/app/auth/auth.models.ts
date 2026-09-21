export type UserRole = 'HR_MANAGER' | 'HR_EXECUTIVE' | 'EMPLOYEE' | 'ADMIN';

export interface SessionUser {
  username: string;
  roles: UserRole[];
}