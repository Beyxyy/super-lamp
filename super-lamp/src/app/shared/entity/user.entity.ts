import { Role } from "@shared/enum/role.enum";

export interface User {
  role: Role;
  username: string;
  create_at: any;
}