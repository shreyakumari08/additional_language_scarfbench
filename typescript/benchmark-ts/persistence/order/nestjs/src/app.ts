import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module.js';
import { NestExpressApplication } from '@nestjs/platform-express';

async function bootstrap() {
  const app = await NestFactory.create<NestExpressApplication>(AppModule, { logger: false });
  await app.listen(8081, '0.0.0.0');
  console.log(`NestJS order on 8081`);
}
bootstrap();
