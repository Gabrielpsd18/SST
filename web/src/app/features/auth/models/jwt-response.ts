export interface JwtResponse {
    accessToken: string;
    tokenType: string;
    expiresIn: number;
    email: string;
    nombreCompleto: string;
    role: string;
}