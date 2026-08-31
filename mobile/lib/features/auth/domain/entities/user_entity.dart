class UserEntity {
  final String email;
  final String nombreCompleto;
  final String role;
  final String accessToken;
  final String tokenType;
  final int expiresIn;

  const UserEntity({
    required this.email,
    required this.nombreCompleto,
    required this.role,
    required this.accessToken,
    this.tokenType = 'Bearer',
    this.expiresIn = 3600,
  });

  bool get isTrabajador => role.toUpperCase() == 'TRABAJADOR';

  factory UserEntity.fromJson(Map<String, dynamic> json) {
    return UserEntity(
      email: json['email'] ?? '',
      nombreCompleto: json['nombreCompleto'] ?? '',
      role: json['role'] ?? '',
      accessToken: json['accessToken'] ?? '',
      tokenType: json['tokenType'] ?? 'Bearer',
      expiresIn: json['expiresIn'] is int ? json['expiresIn'] : int.tryParse('${json['expiresIn']}') ?? 3600,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'email': email,
      'nombreCompleto': nombreCompleto,
      'role': role,
      'accessToken': accessToken,
      'tokenType': tokenType,
      'expiresIn': expiresIn,
    };
  }
}
