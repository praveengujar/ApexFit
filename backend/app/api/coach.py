"""AI Coach conversation routes."""

from __future__ import annotations

import uuid

from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.auth.dependencies import get_current_user
from app.auth.models import AuthUser
from app.core.exceptions import NotFoundError
from app.db.repositories import coach_repo, user_repo
from app.db.session import get_session
from app.schemas.coach import (
    CoachConversationResponse,
    CoachMessageCreate,
    CoachMessageResponse,
)

router = APIRouter()


@router.post("/message", response_model=CoachMessageResponse)
async def send_message(
    body: CoachMessageCreate,
    current_user: AuthUser = Depends(get_current_user),
    session: AsyncSession = Depends(get_session),
) -> CoachMessageResponse:
    user = await user_repo.get_by_firebase_uid(session, current_user.uid)
    if not user:
        user = await user_repo.create(
            session, firebase_uid=current_user.uid, email=current_user.email
        )

    # Get or create conversation
    if body.conversation_id:
        conv = await coach_repo.get_conversation(session, body.conversation_id)
        if not conv or conv.user_id != user.id:
            raise NotFoundError("Conversation")
    else:
        conv = await coach_repo.create_conversation(session, user.id)

    # Save user message
    await coach_repo.add_message(session, conv.id, "user", body.content)

    # Generate AI response (placeholder — wired to CoachService in production)
    ai_content = (
        "I'm your Zyva AI coach. Based on your recent metrics, "
        "I recommend focusing on recovery today. How can I help you further?"
    )
    ai_msg = await coach_repo.add_message(session, conv.id, "assistant", ai_content)

    return CoachMessageResponse.model_validate(ai_msg)


@router.get("/conversations", response_model=list[CoachConversationResponse])
async def list_conversations(
    current_user: AuthUser = Depends(get_current_user),
    session: AsyncSession = Depends(get_session),
) -> list[CoachConversationResponse]:
    user = await user_repo.get_by_firebase_uid(session, current_user.uid)
    if not user:
        return []

    convos = await coach_repo.list_conversations(session, user.id)
    return [CoachConversationResponse.model_validate(c) for c in convos]


@router.get("/conversations/{conversation_id}", response_model=CoachConversationResponse)
async def get_conversation(
    conversation_id: uuid.UUID,
    current_user: AuthUser = Depends(get_current_user),
    session: AsyncSession = Depends(get_session),
) -> CoachConversationResponse:
    user = await user_repo.get_by_firebase_uid(session, current_user.uid)
    if not user:
        raise NotFoundError("User")

    conv = await coach_repo.get_conversation(session, conversation_id)
    if not conv or conv.user_id != user.id:
        raise NotFoundError("Conversation")

    return CoachConversationResponse.model_validate(conv)
