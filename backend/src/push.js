import admin from 'firebase-admin';
import { config } from './config.js';
import { query } from './db.js';

let initialized=false;

function init() {
  if(initialized)return true;
  if(!config.fcmServiceAccountJson)return false;
  try {
    const account=JSON.parse(config.fcmServiceAccountJson);
    if(!admin.apps.length)admin.initializeApp({credential:admin.credential.cert(account)});
    initialized=true;
  } catch {
    initialized=false;
  }
  return initialized;
}

async function tokensFor(userIds) {
  if(!userIds.length)return [];
  const result=await query(
    'select token from device_tokens where user_id = any($1::text[]) and revoked_at is null',
    [userIds]
  );
  return result.rows.map(row=>row.token).filter(Boolean);
}

export async function sendEmergencyPush(userIds,payload) {
  const tokens=await tokensFor(userIds);
  if(!init()||!tokens.length)return{sent:0,skipped:true};
  const response=await admin.messaging().sendEachForMulticast({
    tokens,
    data:{
      messageType:'emergency',
      emergencyId:String(payload?.emergencyId||''),
      status:String(payload?.status||'')
    },
    android:{
      priority:'high',
      ttl:300000
    }
  });
  return{sent:response.successCount,failed:response.failureCount};
}

export async function sendTrustedInvitePush(userId) {
  const tokens=await tokensFor([userId]);
  if(!init()||!tokens.length)return{sent:0,skipped:true};
  const response=await admin.messaging().sendEachForMulticast({
    tokens,
    data:{messageType:'trusted_invite'},
    android:{priority:'high',ttl:86400000}
  });
  return{sent:response.successCount,failed:response.failureCount};
}
