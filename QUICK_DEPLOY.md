# Quick Deployment Guide - Railway (5 minutes)

## Step 1: Push to GitHub
```bash
git add .
git commit -m "Ready for deployment"
git push origin main
```

## Step 2: Deploy on Railway

1. Go to https://railway.app
2. Sign up with GitHub
3. Click **"New Project"** → **"Deploy from GitHub repo"**
4. Select your `wiki` repository
5. Railway auto-detects Spring Boot - click **"Deploy"**

## Step 3: Add PostgreSQL Database

1. In your Railway project, click **"New"** → **"Database"** → **"PostgreSQL"**
2. Railway creates database automatically
3. Go back to your backend service
4. Click **"Variables"** tab
5. Railway automatically adds `DATABASE_URL` - verify it's there

## Step 4: Add Environment Variables

In backend service → Variables tab, add:

```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=generate-a-random-32-character-string-here
```

**Generate JWT Secret:**
```bash
# On Linux/Mac
openssl rand -base64 32

# Or use online generator
```

## Step 5: Get Your URL

1. Click on your backend service
2. Go to **"Settings"** tab
3. Click **"Generate Domain"**
4. Copy the URL (e.g., `https://wiki-production.up.railway.app`)

## Step 6: Share with Frontend Developer

Your API base URL: `https://your-app.up.railway.app/api`

**Test it:**
```bash
curl https://your-app.up.railway.app/actuator/health
```

## Done! 🎉

Your backend is live and ready for frontend integration.

---

## Troubleshooting

**Build fails?**
- Check Railway logs
- Ensure Java 21 is in pom.xml (already set)

**Database connection error?**
- Verify `DATABASE_URL` is set
- Check database is running (green status)

**App won't start?**
- Check logs in Railway dashboard
- Verify all environment variables are set

