# Wiki Backend Deployment Guide

This guide covers deploying the Wiki Inventory & POS System backend to free hosting services.

## Option 1: Railway (Recommended - Easiest)

Railway offers free tier with $5 credit monthly, perfect for MVP.

### Step 1: Prepare Repository
1. Push your code to GitHub
2. Make sure all files are committed

### Step 2: Deploy Backend
1. Go to [railway.app](https://railway.app)
2. Sign up with GitHub
3. Click "New Project" → "Deploy from GitHub repo"
4. Select your repository
5. Railway will auto-detect Spring Boot
6. Add environment variables:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `JWT_SECRET=your-secure-secret-key-here-min-32-chars`
   - `PORT=8081` (Railway sets this automatically, but include it)

### Step 3: Deploy PostgreSQL Database
1. In Railway project, click "New" → "Database" → "PostgreSQL"
2. Railway will create database automatically
3. Copy the connection details
4. Add to backend service environment variables:
   - `DATABASE_URL` (Railway provides this automatically)
   - Or manually set:
     - `DB_USERNAME=postgres`
     - `DB_PASSWORD=<from-railway>`
     - `DATABASE_URL=jdbc:postgresql://<host>:<port>/railway`

### Step 4: Get Your Backend URL
- Railway provides a public URL like: `https://your-app.up.railway.app`
- Your API will be available at: `https://your-app.up.railway.app/api`

---

## Option 2: Render (Free Tier Available)

### Step 1: Deploy PostgreSQL
1. Go to [render.com](https://render.com)
2. Sign up
3. Click "New" → "PostgreSQL"
4. Choose free tier
5. Copy connection string

### Step 2: Deploy Backend
1. Click "New" → "Web Service"
2. Connect your GitHub repository
3. Settings:
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/wiki-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`
   - **Environment**: Java
4. Add Environment Variables:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `DATABASE_URL=<your-postgres-connection-string>`
   - `JWT_SECRET=<your-secret-key>`
   - `PORT=10000` (Render uses port 10000)

### Step 3: Get URL
- Render provides: `https://your-app.onrender.com`
- API: `https://your-app.onrender.com/api`

**Note**: Render free tier spins down after 15 minutes of inactivity (cold start ~30 seconds)

---

## Option 3: Fly.io (Free Tier)

### Step 1: Install Fly CLI
```bash
curl -L https://fly.io/install.sh | sh
```

### Step 2: Login and Initialize
```bash
fly auth login
fly launch
```

### Step 3: Create PostgreSQL Database
```bash
fly postgres create --name wiki-db
fly postgres attach wiki-db
```

### Step 4: Deploy
```bash
fly deploy
```

---

## Environment Variables Summary

All services need these variables:

```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://host:port/database
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your-256-bit-secret-key-must-be-at-least-32-characters-long
PORT=8081  # (usually set automatically by hosting service)
```

---

## Quick Start: Railway (Fastest)

1. **Push to GitHub**
   ```bash
   git add .
   git commit -m "Ready for deployment"
   git push origin main
   ```

2. **Deploy on Railway**:
   - Visit railway.app
   - New Project → Deploy from GitHub
   - Select repo
   - Add PostgreSQL database
   - Add environment variables
   - Deploy!

3. **Get your URL**:
   - Railway provides public URL
   - Share with frontend developer: `https://your-app.up.railway.app/api`

---

## Testing Deployment

After deployment, test with:

```bash
# Health check
curl https://your-app.up.railway.app/actuator/health

# Register (should work)
curl -X POST https://your-app.up.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "companyName": "Test Store",
    "role": "OWNER"
  }'
```

---

## Important Notes

1. **JWT Secret**: Generate a secure random string (32+ characters) for production
2. **Database**: Free tiers have limitations (usually 512MB-1GB storage)
3. **Cold Starts**: Free tiers may have cold start delays
4. **CORS**: Update `WebConfig.java` with your frontend URL
5. **HTTPS**: All services provide HTTPS automatically

---

## Troubleshooting

### Database Connection Issues
- Check `DATABASE_URL` format
- Verify database is running
- Check firewall/network settings

### Build Failures
- Ensure Java 21 is specified
- Check Maven dependencies
- Review build logs

### Application Won't Start
- Check logs in hosting dashboard
- Verify environment variables
- Ensure port is correctly configured

---

## Recommended: Railway

**Why Railway?**
- ✅ Easiest setup
- ✅ Free tier with $5 credit
- ✅ Auto-detects Spring Boot
- ✅ Built-in PostgreSQL
- ✅ Automatic HTTPS
- ✅ Good documentation

**Limitations:**
- Free tier: $5 credit/month
- Sleeps after inactivity (but wakes quickly)

---

## Next Steps After Deployment

1. Update frontend API base URL
2. Test all endpoints
3. Monitor logs for errors
4. Set up proper JWT secret (not default)
5. Configure CORS for your frontend domain

