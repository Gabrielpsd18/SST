import 'dart:io' show Platform;
import 'package:flutter/foundation.dart';

class ApiConstants {
  ApiConstants._();

  // Permite inyectar URL en build: flutter run --dart-define=API_URL=https://api.tu-servidor.com/api/v1
  static const String _envApiUrl = String.fromEnvironment('API_URL');

  // URL para producción / deploy por defecto cuando no se pasa dart-define
  static const String prodBaseUrl = 'https://api.sst-empresa.com/api/v1';

  static String get baseUrl {
    if (_envApiUrl.isNotEmpty) {
      return _envApiUrl;
    }

    if (kReleaseMode) {
      return prodBaseUrl;
    }

    // Entorno local de desarrollo
    if (kIsWeb) {
      return 'http://localhost:8080/api/v1';
    }

    try {
      if (Platform.isAndroid) {
        // Alias de localhost para el emulador de Android
        return 'http://10.0.2.2:8080/api/v1';
      }
    } catch (_) {
      // Fallback seguro si Platform no está disponible
    }

    return 'http://localhost:8080/api/v1';
  }

  // Endpoints
  static const String loginMobile = '/auth/mobile/login';
  static const String loginWeb = '/auth/login';
  static const String documentosPersonales = '/documentos/personales';
  static const String capacitaciones = '/capacitaciones';
  static const String capacitacionesMobile = '/capacitaciones/mobile';
  static const String dashboard = '/dashboard';

  // Configuración de timeouts
  static const Duration connectTimeout = Duration(seconds: 15);
  static const Duration receiveTimeout = Duration(seconds: 15);
}
