import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module.js';
import { NestExpressApplication } from '@nestjs/platform-express';

async function bootstrap() {
  const app = await NestFactory.create<NestExpressApplication>(AppModule, { logger: false });
  await app.listen(9080, '0.0.0.0');
  console.log(`NestJS ejb-timersession on 9080`);
}
bootstrap();
